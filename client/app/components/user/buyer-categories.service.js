(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('buyerCategories', buyerCategories);

    buyerCategories.$inject = ['$http', 'CONFIG'];

    function buyerCategories($http, CONFIG) {
        var service = {
            getCategories: getCategories,
            createCategory: createCategory,
            getThresholdsFrom: getThresholdsFrom,
            addThresholdTo: addThresholdTo,
            removeThreshold: removeThreshold
        };

        return service;

        function getCategories() {
            return $http.get(CONFIG.SERVICE_URL + '/buyer-categories')
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function createCategory(category) {
            return $http.post(CONFIG.SERVICE_URL + '/buyer-categories', category)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function getThresholdsFrom(url) {
            return $http.get(CONFIG.SERVICE_BASE_URL + url)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function addThresholdTo(categoryId, threshold) {
            return $http.post(CONFIG.SERVICE_URL + '/buyer-categories/' + categoryId + '/thresholds', threshold)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function removeThreshold(categoryId, thresholdId) {
            return $http.delete(CONFIG.SERVICE_URL + '/buyer-categories/' + categoryId + '/thresholds/' + thresholdId)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

    }
})();
