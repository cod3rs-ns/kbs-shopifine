(function() {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$log', 'CONFIG', '_', 'productService'];

    function HomeController($log, CONFIG, _, productService) {
        var homeVm = this;

        homeVm.products = [];

        homeVm.retrieveProducts = retrieveProducts;

        init();

        function init() {
            var url = CONFIG.SERVICE_URL + '/products';
            homeVm.retrieveProducts(url)
        }

        function retrieveProducts(url) {
            productService.retrieveAllFrom(url)
                .then(function (response) {
                    $log.info(response);
                    homeVm.products = _.map(response.data, function (product) {
                        return {
                            'name': product.attributes.name,
                            'price': product.attributes.price,
                            // FIXME Extend Product model with image
                            'preview': "https://supersoccershop.com/image/cache/catalog/jersey-16-17/real-madrid/real-madrid-sergio-ramos-jersey-2016-17-soccer-shirt-kit-600x600.jpg"
                        }
                    });

                    $log.info(response.links.next);
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }
    }
})();
