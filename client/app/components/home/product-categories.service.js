(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('productCategories', productCategories);

    productCategories.$inject = ['$http'];

    function productCategories($http) {
        var service = {
            retrieveFrom: retrieveFrom
        };

        return service;

        function retrieveFrom(url) {
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