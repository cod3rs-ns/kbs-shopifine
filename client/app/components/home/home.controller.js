(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$log', '$localStorage', 'CONFIG', '_', 'productService', 'productCategories', 'discounts'];

    function HomeController($log, $localStorage, CONFIG, _, productService, productCategories, discounts) {
        var homeVm = this;

        homeVm.data = {
            products: [],
            categories: [],
            prev: null,
            next: null
        };

        homeVm.filters = {
            'name': undefined,
            'category': undefined,
            'price-range-from': undefined,
            'price-range-to': undefined,
            'active': 'ACTIVE'
        };

        $localStorage.items = [];
        homeVm.productDiscounts = [];

        homeVm.next = retrieveProducts;
        homeVm.prev = retrieveProducts;
        homeVm.retrieveProducts = retrieveProducts;
        homeVm.retrieveCategories = retrieveCategories;
        homeVm.retrieveSubcategoriesFor = retrieveSubcategoriesFor;

        homeVm.applyFilters = applyFilters;
        homeVm.resetFilters = resetFilters;
        homeVm.searchByCategory = searchByCategory;

        homeVm.isCustomer = isCustomer;
        homeVm.addToCart = addToCart;
        homeVm.isInCart = isInCart;
        homeVm.removeFromCart = removeFromCart;
        homeVm.discountsFor = setDialogDiscounts;

        init();

        function init() {
            homeVm.retrieveProducts(CONFIG.SERVICE_URL + '/products?page[limit]=6');
            homeVm.retrieveCategories(CONFIG.SERVICE_URL + '/product-categories');
        }

        function retrieveProducts(url) {
            productService.retrieveAllFrom(url)
                .then(function (response) {
                    homeVm.data.products = _.map(response.data, function (product) {
                        var p = {
                            'id': product.id,
                            'name': product.attributes.name,
                            'price': product.attributes.price,
                            'preview': product.attributes.imageUrl,
                            'discount': 0,
                            'category': '',
                            'discounts': []
                        };

                        productCategories.retrieveFrom(CONFIG.SERVICE_URL + "/product-categories/" + product.relationships.category.data.id)
                            .then(function (response) {
                                p.category = response.data.attributes.name;
                            })
                            .catch(function (data) {
                                $log.error(data);
                            });

                        discounts.retrieveFrom(CONFIG.SERVICE_BASE_URL + product.relationships.discounts.links.related)
                            .then(function (response) {
                                p.discounts = _.forEach(response.data, function (discount) {
                                    p.discount += discount.attributes.discount;
                                    return {
                                        'name': discount.attributes.name,
                                        'from': discount.attributes.from,
                                        'to': discount.attributes.to,
                                        'discount': discount.attributes.discount
                                    }
                                });
                            })
                            .catch(function (data) {
                                $log.error(data);
                            });

                        return p;
                    });

                    var prev = response.links.prev;
                    var next = response.links.next;

                    homeVm.data.prev = (!_.isEmpty(prev)) ? prev : null;
                    homeVm.data.next = (!_.isEmpty(next)) ? next : null;
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function retrieveCategories(url) {
            productCategories.retrieveFrom(url)
                .then(function (response) {
                    homeVm.data.categories = _.concat(homeVm.data.categories, _.reduce(response.data, function (init, category) {
                        if (_.isUndefined(category.relationships.superCategory)) {
                            init.push({
                                'id': category.id,
                                'name': category.attributes.name,
                                'subcategoriesUrl': category.relationships.subcategories.links.related,
                                'subcategories': []
                            });
                        }

                        return init;
                    }, []));

                    var next = response.links.next;
                    if (!_.isEmpty(next)) {
                        homeVm.retrieveCategories(next);
                    }
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function retrieveSubcategoriesFor(category) {
            productCategories.retrieveFrom(CONFIG.SERVICE_BASE_URL + category.subcategoriesUrl)
                .then(function (response) {
                    if (_.isEmpty(category.subcategories)) {
                        category.subcategories = _.map(response.data, function (subcategory) {
                            return {
                                'id': subcategory.id,
                                'name': subcategory.attributes.name,
                                'subcategoriesUrl': subcategory.relationships.subcategories.links.related,
                                'subcategories': []
                            }
                        });
                    }
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function applyFilters() {
            var filters = '';
            _.forEach(homeVm.filters, function (value, name) {
                if (!_.isUndefined(value) && !_.isEmpty(value)) {
                    filters += '&filter[' + name + ']=' + value;
                }
            });

            retrieveProducts(CONFIG.SERVICE_URL + '/products?page[limit]=6' + filters)
        }

        function resetFilters() {
            _.forEach(homeVm.filters, function (value, name) {
                if (name !== 'active') {
                    homeVm.filters[name] = undefined;
                }
            });

            applyFilters();
        }

        function searchByCategory(id) {
            homeVm.filters['category'] = id;
            applyFilters();
        }

        function addToCart(product) {
            $localStorage.items.push({
                'quantity': product.quantity,
                'product': product
            });
        }

        function removeFromCart(productId) {
            _.remove($localStorage.items, function (item) {
                return item.product.id === productId;
            });
        }

        function isInCart(productId) {
            var items = _.find($localStorage.items, function (item) {
                return item.product.id === productId;
            });

            return _.size(items) > 0;
        }

        function setDialogDiscounts(product) {
            homeVm.productDiscounts = product.discounts;
        }

        function isCustomer() {
            return !_.isUndefined($localStorage.user) && "CUSTOMER" === $localStorage.user.role;
        }
    }
})();
