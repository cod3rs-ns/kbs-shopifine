(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['$log', 'ngToast', '_', 'users', 'buyerCategories'];

    function RegisterController($log, ngToast, _, users, buyerCategories) {
        var registerVm = this;

        registerVm.error = false;
        registerVm.passwordRepeat = "";
        registerVm.buyerCategories = [];
        registerVm.user = {
            "data": {
                "type": "users",
                "attributes": {
                    "username": "",
                    "password": "",
                    "firstName": "",
                    "lastName": "",
                    "role": "CUSTOMER",
                    "address": null
                }
            }
        };

        registerVm.relationships = {
            "buyerCategory": {
                "data": {
                    "type": "buyer-categories",
                    "id": undefined
                }
            }
        };

        registerVm.getBuyerCategories = getBuyerCategories;
        registerVm.register = register;
        registerVm.isCustomer = isCustomer;

        init();

        function init() {
            getBuyerCategories();
        }

        function getBuyerCategories() {
            buyerCategories.getCategories()
                .then(function (response) {
                    _.forEach(response.data, function (category) {
                        registerVm.buyerCategories.push({
                            'id': category.id,
                            'name': category.attributes.name
                        });
                    });

                    registerVm.relationships.buyerCategory = _.head(registerVm.buyerCategories).id;
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function register() {
            if (!validate()) return;

            if (!isCustomer()) {
                registerVm.user.data.relationships = null;
                registerVm.user.data.attributes.address = null;
            }
            else {
                registerVm.user.data.relationships = {
                    'buyerCategory': {
                        'data': {
                            'type': "buyer-categories",
                            'id': registerVm.relationships.buyerCategory
                        }
                    }
                }
            }

            users.register(registerVm.user)
                .then(function (response) {
                    registerVm.error = false;

                    ngToast.success({
                        content: 'Welcome to Shopifine, ' + response.data.attributes.firstName + ' ' + response.data.attributes.lastName + '. Now you can go to login page.'
                    });
                })
                .catch(function (data) {
                    registerVm.error = true;
                    registerVm.message = _.head(data.errors).detail;
                });
        }

        function isCustomer() {
            return !_.isEmpty(registerVm.user.data.attributes.role) && "CUSTOMER" === registerVm.user.data.attributes.role;
        }

        function validate() {
            if (registerVm.user.data.attributes.username.length < 8) {
                registerVm.error = true;
                registerVm.message = "Username must be longer than 8 characters.";
                return false;
            }

            if (_.isEmpty(registerVm.user.data.attributes.firstName) || _.isEmpty(registerVm.user.data.attributes.lastName)) {
                registerVm.error = true;
                registerVm.message = "You must specify First and Last name.";
                return false;
            }

            if (registerVm.user.data.attributes.password.length < 8) {
                registerVm.error = true;
                registerVm.message = "Password must contain minimum 8 characters.";
                return false;
            }

            if (isCustomer() && _.isEmpty(registerVm.user.data.attributes.address)) {
                registerVm.error = true;
                registerVm.message = "You must specify your Shipping Address.";
                return false;
            }

            if (registerVm.passwordRepeat !== registerVm.user.data.attributes.password) {
                registerVm.error = true;
                registerVm.message = "Password do not match!";
                return false;
            }

            return true;
        }
    }
})();
