(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$log', 'CONFIG', '_', 'productService'];

    function HomeController($log, CONFIG, _, productService) {
        var homeVm = this;

        homeVm.data = {
            products: [],
            prev: null,
            next: null
        };

        homeVm.next = retrieveProducts;
        homeVm.prev = retrieveProducts;
        homeVm.retrieveProducts = retrieveProducts;

        init();

        function init() {
            var url = CONFIG.SERVICE_URL + '/products?page[limit]=6';
            homeVm.retrieveProducts(url)
        }

        function retrieveProducts(url) {
            productService.retrieveAllFrom(url)
                .then(function (response) {
                    $log.info(response);
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
    }
})();
