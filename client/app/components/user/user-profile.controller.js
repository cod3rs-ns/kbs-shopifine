(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('UserProfileController', UserProfileController);

    UserProfileController.$inject = [
        '$log',
        '$localStorage',
        'ngToast',
        'users',
        'bills',
        'buyerCategories',
        'productCategories',
        'discounts',
        'productService'
    ];

    function UserProfileController($log, $localStorage, ngToast, users, bills, buyerCategories, productCategories, discounts, productService) {
        var profileVm = this;

        profileVm.user = undefined;

        profileVm.me = me;
        profileVm.retrieveBills = retrieveBills;

        // Thresholds and Buyer Categories
        profileVm.retrieveBuyerCategories = retrieveBuyerCategories;
        profileVm.addBuyerCategory = addBuyerCategory;
        profileVm.modifyBuyerCategory = modifyBuyerCategory;
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
        profileVm.cancelBill = cancelBill;

        init();

        function init() {
            profileVm.me();
        }

        function me() {
            var id = $localStorage.user.id;
            users.findBy(id)
                .then(function (response) {
                    profileVm.user = response.data.attributes;

                    if (response.data.attributes.role === 'CUSTOMER') {
                        profileVm.user.bills = [];
                        retrieveBills(id);
                    }
                    else if (response.data.attributes.role === 'SALES_MANAGER') {
                        profileVm.user.buyerCategories = [];
                        profileVm.user.productCategories = [];
                        profileVm.user.actionDiscounts = [];
                        profileVm.actionDiscount = {
                            'new': {
                                'categories': []
                            }
                        };
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
                    _.forEach(response.data, function (bill) {
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

        function retrieveBuyerCategories() {
            buyerCategories.getCategories()
                .then(function (response) {
                    _.forEach(response.data, function (category) {
                        var c = {
                            'id': category.id,
                            'name': category.attributes.name,
                            'thresholds': [],
                            'edit': false,
                            'edited': {
                                'name': category.attributes.name
                            }
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
                    ngToast.success({
                        content: 'Buyer Category successfully created!'
                    });

                    profileVm.user.buyerCategories.push({
                        'id': response.data.id,
                        'name': response.data.attributes.name,
                        'edit': false,
                        'edited': {
                            'name': response.data.attributes.name
                        },
                        'thresholds': []
                    });

                    profileVm.buyerCategories.new.name = "";
                })
                .catch(function (data) {
                    $log.error(data);
                })
        }

        function modifyBuyerCategory(category) {
            var request = {
                'data': {
                    'type': "buyer-categories",
                    'attributes': {
                        'name': category.edited.name
                    }
                }
            };

            buyerCategories.modify(category.id, request)
                .then(function (response) {
                    ngToast.success({
                        content: 'Buyer Category successfully modified!'
                    });

                    _.forEach(profileVm.user.buyerCategories, function (c) {
                        var attributes = response.data.attributes;
                        if (c.id === response.data.id) {
                            c.name = attributes.name;
                            c.edited = {
                                'name': attributes.name
                            };
                            c.edit = false;
                        }
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function addThresholdFor(category) {
            var award = parseFloat(category.threshold.new.award);
            if (_.isNaN(award)) {
                ngToast.danger({
                    content: 'Threshold award must be between 0 and 99.'
                });
                return;
            }

            var request = {
                'data': {
                    'type': 'consumption-thresholds',
                    'attributes': {
                        'from': parseFloat(category.threshold.new.from),
                        'to': parseFloat(category.threshold.new.to),
                        'award': award
                    },
                    'relationships': {
                        'category': {
                            'data': {
                                'type': "buyer-categories",
                                'id': _.parseInt(category.id)
                            }
                        }
                    }
                }
            };

            buyerCategories.addThresholdTo(category.id, request)
                .then(function (response) {
                    ngToast.success({
                        content: 'Threshold successfully added to Buyer Category!'
                    });

                    _.forEach(profileVm.user.buyerCategories, function (c) {
                        if (c.id === category.id) {
                            c.thresholds.push({
                                'id': response.data.id,
                                'from': response.data.attributes.from,
                                'to': response.data.attributes.to,
                                'award': response.data.attributes.award
                            });

                            c.threshold.new = {
                                'from': '',
                                'to': '',
                                'award': ''
                            }
                        }
                    });
                })
                .catch(function (data) {
                    if (!_.isUndefined(data.errors)) {
                        var error = _.first(data.errors);

                        ngToast.danger({
                            content: error.detail
                        });
                    }
                })
        }

        function removeThreshold(categoryId, thresholdId) {
            buyerCategories.removeThreshold(categoryId, thresholdId)
                .then(function () {
                    ngToast.danger({
                        content: 'Successfully removed threshold!'
                    });

                    _.forEach(profileVm.user.buyerCategories, function (c) {
                        if (c.id === categoryId) {
                            c.thresholds = _.filter(c.thresholds, function (t) {
                                return t.id !== thresholdId;
                            });
                        }
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                })
        }

        function retrieveProductCategories() {
            productCategories.getAll()
                .then(function (response) {
                    _.forEach(response.data, function (category) {
                        var superCategory = category.relationships.superCategory;
                        var superCategoryId = "0";
                        var superCategoryName = "";
                        if (!_.isUndefined(superCategory)) {
                            superCategoryId = superCategory.data.id;
                            superCategoryName = _.head(_.filter(profileVm.user.productCategories, function (c) {
                                return _.parseInt(c.id) === _.parseInt(superCategoryId);
                            })).name;
                        }

                        var c = {
                            'id': category.id,
                            'name': category.attributes.name,
                            'isConsumerGoods': category.attributes.isConsumerGoods,
                            'maxDiscount': category.attributes.maxDiscount,
                            'superCategory': {
                                'id': superCategoryId,
                                'name': superCategoryName
                            },
                            'edit': false,
                            'edited': {
                                'name': category.attributes.name,
                                'maxDiscount': category.attributes.maxDiscount,
                                'isConsumerGoods': category.attributes.isConsumerGoods,
                                'superCategory': superCategoryId
                            }
                        };

                        profileVm.user.productCategories.push(c);
                    });

                    profileVm.actionDiscount.new.actionDiscountProductCategory = _.head(profileVm.user.productCategories);
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
                    }
                }
            };

            if (_.parseInt(profileVm.productCategory.new.superCategory) !== 0) {
                request.data['relationships'] =
                {
                    'superCategory': {
                        'data': {
                            'type': 'product-categories',
                                'id': _.parseInt(profileVm.productCategory.new.superCategory)
                        }
                    }
                }
            }

            productCategories.create(request)
                .then(function (response) {
                    ngToast.success({
                        content: 'Product Category successfully created!'
                    });

                    var superCategory = response.data.relationships.superCategory;
                    var superCategoryId = "0";
                    var superCategoryName = "";
                    if (!_.isUndefined(superCategory)) {
                        superCategoryId = superCategory.data.id;
                        superCategoryName = _.head(_.filter(profileVm.user.productCategories, function (c) {
                            return _.parseInt(c.id) === _.parseInt(superCategoryId);
                        })).name;
                    }

                    profileVm.user.productCategories.push({
                        'id': response.data.id,
                        'name': response.data.attributes.name,
                        'maxDiscount': response.data.attributes.maxDiscount,
                        'superCategory': {
                            'id': superCategoryId,
                            'name': superCategoryName
                        },
                        'edit': false,
                        'edited': {
                            'name': response.data.attributes.name,
                            'maxDiscount': response.data.attributes.maxDiscount,
                            'isConsumerGoods': response.data.attributes.isConsumerGoods,
                            'superCategory': superCategoryId
                        }
                    });

                    profileVm.productCategory.new = {
                        'name': '',
                        'maxDiscount': '',
                        'superCategory': 0,
                        'isConsumerGoods': false
                    }
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function modifyProductCategory(category) {
            var request = {
                'data': {
                    'type': "product-categories",
                    'attributes': {
                        'name': category.edited.name,
                        'maxDiscount': parseFloat(category.edited.maxDiscount),
                        'isConsumerGoods': category.edited.isConsumerGoods
                    }
                }
            };

            if (_.parseInt(category.edited.superCategory) !== 0) {
                request.data['relationships'] =
                    {
                        'superCategory': {
                            'data': {
                                'type': 'product-categories',
                                'id': _.parseInt(category.edited.superCategory)
                            }
                        }
                    }
            }

            productCategories.modify(category.id, request)
                .then(function (response) {
                    ngToast.success({
                        content: 'Product Category successfully modified!'
                    });

                    _.forEach(profileVm.user.productCategories, function (c) {
                        var attributes = response.data.attributes;
                        if (c.id === response.data.id) {
                            var superCategory = response.data.relationships.superCategory;
                            var superCategoryId = "0";
                            var superCategoryName = "";
                            if (!_.isUndefined(superCategory)) {
                                superCategoryId = superCategory.data.id;
                                superCategoryName = _.head(_.filter(profileVm.user.productCategories, function (c) {
                                    return _.parseInt(c.id) === _.parseInt(superCategoryId);
                                })).name;
                            }

                            c.name = attributes.name;
                            c.maxDiscount = attributes.maxDiscount;
                            c.isConsumerGoods = attributes.isConsumerGoods;
                            c.superCategory = {
                                'id': superCategoryId,
                                'name': superCategoryName
                            };
                            c.edited = {
                                'name': attributes.name,
                                'maxDiscount': attributes.maxDiscount,
                                'isConsumerGoods': attributes.isConsumerGoods,
                                'superCategory': superCategoryId
                            };
                            c.edit = false;
                        }
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function retrieveActionDiscounts() {
            discounts.getAll()
                .then(function (response) {
                    _.forEach(response.data, function (discount) {
                        profileVm.user.actionDiscounts.push({
                            'id': discount.id,
                            'name': discount.attributes.name,
                            'from': discount.attributes.from,
                            'to': discount.attributes.to,
                            'discount': discount.attributes.discount,
                            'edit': false,
                            'edited': {
                                'name': discount.attributes.name,
                                'from': new Date(discount.attributes.from),
                                'to': new Date(discount.attributes.to),
                                'discount': discount.attributes.discount
                            }
                        });
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function addActionDiscount() {
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
                    _.forEach(profileVm.actionDiscount.new.categories, function (category) {
                        var discountId = response.data.id;
                        discounts.addProductCategory(discountId, category.id)
                            .then(function () {
                                $log.info("Added Product Category " + category.name + ".");
                            })
                            .catch(function (data) {
                                $log.error(data);
                            });
                    });

                    ngToast.success({
                        content: 'Action Discount successfully created.'
                    });

                    profileVm.user.actionDiscounts.push({
                        'id': response.data.id,
                        'name': response.data.attributes.name,
                        'from': response.data.attributes.from,
                        'to': response.data.attributes.to,
                        'discount': response.data.attributes.discount,
                        'edit': false,
                        'edited': {
                            'name': response.data.attributes.name,
                            'from': new Date(response.data.attributes.from),
                            'to': new Date(response.data.attributes.to),
                            'discount': response.data.attributes.discount
                        }
                    });

                    profileVm.actionDiscount.new = {
                        'name': '',
                        'from': null,
                        'to': null,
                        'discount': '',
                        'actionDiscountProductCategory': _.head(profileVm.user.productCategories)
                    }

                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function modifyActionDiscount(actionDiscount) {
            var request = {
                'data': {
                    'type': 'action-discounts',
                    'attributes': {
                        'name': actionDiscount.edited.name,
                        'from': actionDiscount.edited.from,
                        'to': actionDiscount.edited.to,
                        'discount': parseFloat(actionDiscount.edited.discount)
                    }
                }
            };

            discounts.modify(actionDiscount.id, request)
                .then(function (response) {
                    ngToast.success({
                        content: 'Action Discount successfully modified!'
                    });

                    _.forEach(profileVm.user.actionDiscounts, function (discount) {
                        var attributes = response.data.attributes;
                        if (discount.id === response.data.id) {
                            discount.name = attributes.name;
                            discount.from = attributes.from;
                            discount.to = attributes.to;
                            discount.discount = attributes.discount;
                            discount.edited = {
                                'name': attributes.name,
                                'from': attributes.from,
                                'to': attributes.to,
                                'discount': attributes.discount
                            };
                            discount.edit = false;
                        }
                    })
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function retrieveOutOfStockProducts() {
            productService.retrieveOutOfStock()
                .then(function (response) {
                    profileVm.user.outOfStockProducts = _.map(response.data, function (product) {
                        return {
                            'id': product.id,
                            'name': product.attributes.name,
                            'quantity': product.attributes.quantity,
                            'minQuantity': product.attributes.minQuantity,
                            'orderQuantity': 0
                        }
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function orderProduct(product) {
            productService.orderProduct(product.id, _.parseInt(product.orderQuantity))
                .then(function (response) {
                    ngToast.success({
                        content: 'Product quantity successfully updated.'
                    });

                    _.forEach(profileVm.user.outOfStockProducts, function (p) {
                        if (p.id === product.id) {
                            p.quantity = response.data.attributes.quantity;
                        }
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function retrieveAllBills() {
            bills.getAll()
                .then(function (response) {
                    _.forEach(response.data, function (bill) {
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
                    ngToast.success({
                        content: 'Bill successfully processed!'
                    });

                    _.forEach(profileVm.user.bills, function (bill) {
                        if (bill.id === response.data.id) {
                            bill.status = response.data.attributes.state;
                        }
                    });
                })
                .catch(function (data) {
                    ngToast.danger({
                        content: _.head(data.errors).detail
                    });
                });
        }

        function cancelBill(id) {
            bills.cancel(id)
                .then(function (response) {
                    ngToast.success({
                        content: 'Bill successfully cancelled!'
                    });

                    _.forEach(profileVm.user.bills, function (bill) {
                        if (bill.id === response.data.id) {
                            bill.status = response.data.attributes.state;
                        }
                    });
                })
                .catch(function () {
                    ngToast.danger({
                        content: 'Error in Bill processing!'
                    });
                });
        }
    }
})();
