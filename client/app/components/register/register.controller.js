(function() {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['ngToast', '_', 'users'];

    function RegisterController(ngToast, _, users) {
        var registerVm = this;

        // FIXME Extract toDto function
        registerVm.error = false;
        registerVm.passwordRepeat = "";
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
                    "id": null
                }
            }
        };

        registerVm.register = register;
        registerVm.isCustomer = isCustomer;

        function register() {
            if (registerVm.passwordRepeat !== registerVm.user.data.attributes.password) {
                registerVm.error = true;
                registerVm.message = "Password do not match!";
                return;
            }

            // FIXME Better implementation
            if (!isCustomer()) {
                registerVm.user.data.relationships = null;
                registerVm.user.data.attributes.address = null;
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
                    registerVm.message = _.first(data.errors).detail;
                });
        }

        function isCustomer() {
            return !_.isEmpty(registerVm.user.data.attributes.role) && "CUSTOMER" === registerVm.user.data.attributes.role;
        }
    }
})();
