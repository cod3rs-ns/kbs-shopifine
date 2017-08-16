(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('UserProfileController', UserProfileController);

    UserProfileController.$inject = [
        '$log',
        '$localStorage',
        'users',
        'bills',
        'buyerCategories',
        'productCategories',
        'discounts',
        'productService'
    ];

    function UserProfileController($log, $localStorage, users, bills, buyerCategories, productCategories, discounts, productService) {
        var profileVm = this;

        profileVm.user = undefined;

        profileVm.me = me;
        profileVm.retrieveBills = retrieveBills;

        // Thresholds and Buyer Categories
        profileVm.retrieveBuyerCategories = retrieveBuyerCategories;
        profileVm.addBuyerCategory = addBuyerCategory;
        profileVm.addThresholdFor = addThresholdFor;
        profileVm.removeThreshold = removeThreshold;

        // Product Categories
        profileVm.retrieveProductCategories = retrieveProductCategories;
        profileVm.addProductCategory = addProductCategory;
        profileVm.modifyProductCategory = modifyProductCategory;

        // Action Discounts
        profileVm.retrieveActionDiscounts = retrieveActionDiscounts;
        profileVm.addActionDiscount = addActionDiscount;
        profileVm.modifyActionDiscount = modifyActionDiscount;

        // Salesman Products and Bills
        profileVm.retrieveOutOfStockProducts = retrieveOutOfStockProducts;
        profileVm.orderProduct = orderProduct;

        profileVm.retrieveAllBills = retrieveAllBills;
        profileVm.confirmBill = confirmBill;

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
                        profileVm.user.productCategories = [];
                        profileVm.user.actionDiscounts = [];
                        retrieveBuyerCategories();
                        retrieveProductCategories();
                        retrieveActionDiscounts();
                    }
                    else if (response.data.attributes.role === 'SALESMAN') {
                        profileVm.user.outOfStockProducts = [];
                        profileVm.user.bills = [];
                        retrieveOutOfStockProducts();
                        retrieveAllBills();
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

        function retrieveProductCategories() {
            productCategories.getAll()
                .then(function (response) {
                    _.forEach(response.data, function(category) {
                        var c = {
                            'id': category.id,
                            'name': category.attributes.name,
                            'maxDiscount': category.attributes.maxDiscount
                        };

                        profileVm.user.productCategories.push(c);
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function addProductCategory() {
            var request = {
                'data': {
                    'type': "product-categories",
                    'attributes': {
                        'name': profileVm.productCategory.new.name,
                        'maxDiscount': parseFloat(profileVm.productCategory.new.maxDiscount),
                        'isConsumerGoods': profileVm.productCategory.new.isConsumerGoods
                    },
                    'relationships': {
                        'superCategory': {
                            'data': {
                                'type': 'product-categories',
                                'id': _.parseInt(profileVm.productCategory.new.superCategory)
                            }
                        }
                    }
                }
            };

            productCategories.create(request)
                .then(function (response) {
                   $log.info(response);
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function modifyProductCategory(id) {
            $log.info(id);
        }

        function retrieveActionDiscounts() {
            discounts.getAll()
                .then(function (response) {
                    $log.info(response);
                    _.forEach(response.data, function(discount) {
                        profileVm.user.actionDiscounts.push({
                            'id': discount.id,
                            'name': discount.attributes.name,
                            'from': discount.attributes.from,
                            'to': discount.attributes.to,
                            'discount': discount.attributes.discount
                        });
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function addActionDiscount() {
            $log.info(profileVm.actionDiscount.new);
            var request = {
                'data': {
                    'type': "action-discounts",
                    'attributes': {
                        'name': profileVm.actionDiscount.new.name,
                        'from': profileVm.actionDiscount.new.from,
                        'to': profileVm.actionDiscount.new.to,
                        'discount': parseFloat(profileVm.actionDiscount.new.discount)
                    }
                }
            };

            discounts.create(request)
                .then(function (response) {
                    $log.info(response.data);
                })
                .catch(function (data) {
                   $log.error(data);
                });
        }

        function modifyActionDiscount(id) {
            $log.info(id);
        }

        function retrieveOutOfStockProducts() {
            productService.retrieveOutOfStock()
                .then(function (response) {
                    profileVm.user.outOfStockProducts = _.map(response.data, function (product) {
                        return {
                            'id': product.id,
                            'name': product.attributes.name,
                            'quantity': product.attributes.quantity,
                            'minQuantity': product.attributes.minQuantity
                        }
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function orderProduct(productId) {
            productService.orderProduct(productId, _.parseInt(profileVm.user.products.test))
                .then(function (response) {
                    $log.info(response);
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function retrieveAllBills() {
            bills.getAll()
                .then(function (response) {
                    _.forEach(response.data, function(bill) {
                        profileVm.user.bills.push({
                            'id': bill.id,
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

        function confirmBill(id) {
            bills.confirm(id)
                .then(function (response) {
                    $log.info(response);
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

    }
})();
