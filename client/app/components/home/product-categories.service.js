(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('productCategories', productCategories);

    productCategories.$inject = ['$http', 'CONFIG'];

    function productCategories($http, CONFIG) {
        var service = {
            retrieveFrom: retrieveFrom,
            getAll: getAll,
            create: create,
            modify: modify
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

        function getAll() {
            return $http.get(CONFIG.SERVICE_URL + '/product-categories')
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function create(category) {
            return $http.post(CONFIG.SERVICE_URL + '/product-categories', category)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function modify(id, category) {
            return $http.put(CONFIG.SERVICE_URL + '/product-categories/' + id, category)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }
    }
})();