(function() {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('HomeController', HomeController);

    HomeController.$inject = [];

    function HomeController() {
        var homeVm = this;

        homeVm.products = [
            {
                "preview": "https://supersoccershop.com/image/cache/catalog/jersey-16-17/real-madrid/real-madrid-sergio-ramos-jersey-2016-17-soccer-shirt-kit-600x600.jpg",
                "name": "Sergio Ramos Home Jersey 2016/17",
                "price": 21.10
            },
            {
                "preview": "http://www.footballjersey.store/images/real-madrid/sergio-ramos-real-madrid-16-17-white-long-sleeve-jersey.jpg",
                "name": "Sergio Ramos Home Jersey Long Sleeve",
                "price": 42.44
            },
            {
                "preview": "http://www.footballjersey.store/images/real-madrid/sergio-ramos-2016-17-third-black.jpg",
                "name": "Sergio Ramos Away Jersey 2016/17",
                "price": 15.20
            },
            {
                "preview": "http://www.spainsocceronline.com/images/products/soccer_jerseys/nation/spain/nation_spain_485.jpg",
                "name": "Sergio Ramos National Jersey Woman",
                "price": 33.12
            },
            {
                "preview": "http://www.footballjersey.store/images/real-madrid/sergio-ramos-real-madrid-16-17-white-long-sleeve-jersey.jpg",
                "name": "Sergio Ramos Home Jersey Long Sleeve",
                "price": 42.44
            },
            {
                "preview": "http://www.footballjersey.store/images/real-madrid/sergio-ramos-2016-17-third-black.jpg",
                "name": "Sergio Ramos Away Jersey 2016/17",
                "price": 15.20
            }
        ]
    }
})();
