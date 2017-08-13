(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('ShoppingCartController', ShoppingCartController);

    ShoppingCartController.$inject = ['$localStorage'];

    function ShoppingCartController($localStorage) {
        var cartVm = this;

        cartVm.$storage = $localStorage;

        cartVm.confirm = orderItems;

        function orderItems() {

        }
    }

})();
