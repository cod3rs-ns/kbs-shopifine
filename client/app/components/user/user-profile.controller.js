(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('UserProfileController', UserProfileController);

    UserProfileController.$inject = ['$log', '$localStorage', 'users', 'bills', 'buyerCategories'];

    function UserProfileController($log, $localStorage, users, bills, buyerCategories) {
        var profileVm = this;

        profileVm.user = undefined;

        profileVm.me = me;
        profileVm.retrieveBills = retrieveBills;
        profileVm.retrieveBuyerCategories = retrieveBuyerCategories;

        // Thresholds and Buyer Categories
        profileVm.addBuyerCategory = addBuyerCategory;
        profileVm.addThresholdFor = addThresholdFor;
        profileVm.removeThreshold = removeThreshold;

        init();

        function init() {
            profileVm.me();
        }

        function me() {
            var id = $localStorage.user.id;
            users.findBy(id)
                .then(function (response) {
                    profileVm.user = response.data.attributes;
                    $log.info(response.data.attributes.role);
                    if (response.data.attributes.role === 'CUSTOMER') {
                        profileVm.user.bills = [];
                        retrieveBills(id);
                    }
                    else if (response.data.attributes.role === 'SALES_MANAGER') {
                        profileVm.user.buyerCategories = [];
                        retrieveBuyerCategories();
                    }
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function retrieveBills(id) {
            bills.getBillsByUser(id)
                .then(function (response) {
                    _.forEach(response.data, function(bill) {
                        profileVm.user.bills.push({
                            'createdAt': bill.attributes.createdAt,
                            'amount': bill.attributes.amount,
                            'discount': bill.attributes.discount,
                            'discountAmount': bill.attributes.discountAmount,
                            'pointsGained': bill.attributes.pointsGained,
                            'pointsSpent': bill.attributes.pointsSpent,
                            'status': bill.attributes.state
                        });
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function retrieveBuyerCategories() {
            buyerCategories.getCategories()
                .then(function (response) {
                    _.forEach(response.data, function(category) {
                        var c = {
                            'id': category.id,
                            'name': category.attributes.name,
                            'thresholds': []
                        };

                        buyerCategories.getThresholdsFrom(category.relationships.thresholds.links.related)
                            .then(function (response) {
                                _.forEach(response.data, function (threshold) {
                                    c.thresholds.push({
                                        'id': threshold.id,
                                        'from': threshold.attributes.from,
                                        'to': threshold.attributes.to,
                                        'award': threshold.attributes.award
                                    });
                                });
                            })
                            .catch(function (data) {
                                $log.error(data);
                            });

                        profileVm.user.buyerCategories.push(c);

                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function addBuyerCategory() {
            var request = {
                'data': {
                    'type': 'buyer-categories',
                    'attributes': {
                        'name': profileVm.buyerCategories.new.name
                    }
                }
            };

            buyerCategories.createCategory(request)
                .then(function (response) {
                    $log.info(response);
                })
                .catch(function (data) {
                    $log.error(data);
                })
        }

        function addThresholdFor(categoryId) {
            var request = {
                'data': {
                    'type': 'consumption-thresholds',
                    'attributes': {
                        'from': parseFloat(profileVm.thresholds.new.from),
                        'to': parseFloat(profileVm.thresholds.new.to),
                        'award': parseFloat(profileVm.thresholds.new.award)
                    },
                    'relationships': {
                        'category': {
                            'data': {
                                'type': "buyer-categories",
                                'id': _.parseInt(categoryId)
                            }
                        }
                    }
                }
            };

            buyerCategories.addThresholdTo(categoryId, request)
                .then(function (response) {
                    $log.info(response);
                })
                .catch(function (data) {
                    $log.error(data);
                })
        }

        function removeThreshold(categoryId, thresholdId) {
            buyerCategories.removeThreshold(categoryId, thresholdId)
                .then(function (response) {
                    $log.info(response);
                })
                .catch(function (data) {
                    $log.error(data);
                })
        }

    }
})();
