(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$log', '$localStorage', 'CONFIG', '_', 'productService', 'productCategories'];

    function HomeController($log, $localStorage, CONFIG, _, productService, productCategories) {
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

        homeVm.next = retrieveProducts;
        homeVm.prev = retrieveProducts;
        homeVm.retrieveProducts = retrieveProducts;
        homeVm.retrieveCategories = retrieveCategories;
        homeVm.retrieveSubcategoriesFor = retrieveSubcategoriesFor;

        homeVm.applyFilters = applyFilters;
        homeVm.resetFilters = resetFilters;
        homeVm.searchByCategory = searchByCategory;

        homeVm.addToCart = addToCart;

        init();

        function init() {
            homeVm.retrieveProducts(CONFIG.SERVICE_URL + '/products?page[limit]=6');
            homeVm.retrieveCategories(CONFIG.SERVICE_URL + '/product-categories');
        }

        function retrieveProducts(url) {
            productService.retrieveAllFrom(url)
                .then(function (response) {
                    homeVm.data.products = _.map(response.data, function (product) {
                        return {
                            'id': product.id,
                            'name': product.attributes.name,
                            'price': product.attributes.price,
                            // 'preview': product.attributes.imageUrl
                            'preview': 'https://s-media-cache-ak0.pinimg.com/736x/83/37/4e/83374e1f3aa84e3ff3a04a63eda7e4f7--cheap-ray-ban-sunglasses-cheap-ray-bans.jpg'
                        }
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
            productCategories.retrieveAllFrom(url)
                .then(function (response) {
                    homeVm.data.categories = _.concat(homeVm.data.categories, _.map(response.data, function(category) {
                        return {
                            'id': category.id,
                            'name': category.attributes.name,
                            'subcategoriesUrl': category.relationships.subcategories.links.related,
                            'subcategories': []
                        }
                    }));

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
            productCategories.retrieveAllFrom(CONFIG.SERVICE_BASE_URL + category.subcategoriesUrl)
                .then(function (response) {
                    if (_.isEmpty(category.subcategories)) {
                        category.subcategories = _.concat(category.subcategories, _.map(response.data, function (subcategory) {
                            return {
                                'id': subcategory.id,
                                'name': subcategory.attributes.name,
                                'subcategoriesUrl': subcategory.relationships.subcategories.links.related,
                                'subcategories': []
                            }
                        }));
                    }

                    homeVm.filters['category'] = category.id.toString();
                    applyFilters();

                    // FIXME
                    // var next = response.links.next;
                    // if (!_.isEmpty(next)) {
                    //     homeVm.retrieveCategories(next);
                    // }
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function applyFilters() {
            var filters = '';
            _.forEach(homeVm.filters, function(value, name) {
                if (!_.isUndefined(value) && !_.isEmpty(value)) {
                    filters += '&filter[' + name + ']=' + value;
                }
            });

            retrieveProducts(CONFIG.SERVICE_URL + '/products?page[limit]=6' + filters)
        }

        function resetFilters() {
            _.forEach(homeVm.filters, function(value, name) {
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
    }
})();
