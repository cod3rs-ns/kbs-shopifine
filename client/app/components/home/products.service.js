(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('productService', productService);

    productService.$inject = ['$http', 'CONFIG'];

    function productService($http, CONFIG) {
        var service = {
            retrieveAllFrom: retrieveAllFrom,
            retrieveOutOfStock: retrieveOutOfStock,
            orderProduct: orderProduct,
            getOne: getOne
        };

        return service;

        function retrieveAllFrom(url) {
            return $http.get(url)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function retrieveOutOfStock() {
            return $http.get(CONFIG.SERVICE_URL + "/products/out-of-stock")
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function orderProduct(productId, quantity) {
            return $http.put(CONFIG.SERVICE_URL + '/products/' + productId + '?quantity=' + quantity)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function getOne(productId) {
            return $http.get(CONFIG.SERVICE_URL + '/products/' + productId)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }
    }
})();
