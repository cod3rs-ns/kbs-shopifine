(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$log', 'CONFIG', '_', 'productService', 'productCategories'];

    function HomeController($log, CONFIG, _, productService, productCategories) {
        var homeVm = this;

        homeVm.data = {
            products: [],
            categories: [],
            prev: null,
            next: null
        };

        homeVm.next = retrieveProducts;
        homeVm.prev = retrieveProducts;
        homeVm.retrieveProducts = retrieveProducts;
        homeVm.retrieveCategories = retrieveCategories;
        homeVm.retrieveSubcategoriesFor = retrieveSubcategoriesFor;

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
                            'name': product.attributes.name,
                            'price': product.attributes.price,
                            'preview': product.attributes.imageUrl
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
            // FIXME Extract base URL
            productCategories.retrieveAllFrom("http://localhost:9000/" + category.subcategoriesUrl)
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
    }
})();
