(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('discounts', discounts);

    discounts.$inject = ['$http'];

    function discounts($http) {
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