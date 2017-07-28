(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('productService', productService);

    productService.$inject = ['$http'];

    function productService($http) {
        var service = {
            retrieveAllFrom: retrieveAllFrom
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
    }
})();