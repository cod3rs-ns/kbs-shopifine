angular
    .module('shopifine-app')
    .factory('_', ['$window',
        function ($window) {
            // place Lodash include before Angular
            return $window._;
        }
    ])
    .constant(
        'CONFIG', {
            'SERVICE_BASE_URL': 'http://localhost:9000',
            'SERVICE_URL': 'http://localhost:9000/api'
        }
    )
    .config(function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider) {

        // For excluding exclamation from url
        $locationProvider.hashPrefix('');

        // For any unmatched url, redirect to /home
        $urlRouterProvider.otherwise("/home");

        $stateProvider
            .state('home', {
                url: "/home",
                data: {
                    pageTitle: "Shopifine | Home"
                },
                views: {
                    'content@': {
                        templateUrl: "app/components/home/home.html",
                        controller: "HomeController",
                        controllerAs: "homeVm"
                    }
                }
            })
            .state('login', {
                url: "/login",
                data: {
                    pageTitle: "Shopifine | Sign In"
                },
                views: {
                    'content@': {
                        templateUrl: "app/components/login/login.html",
                        controller: "LoginController",
                        controllerAs: "loginVm"
                    }
                }
            })
            .state('register', {
                url: "/register",
                data: {
                    pageTitle: "Shopifine | Sign Up"
                },
                views: {
                    'content@': {
                        templateUrl: "app/components/register/register.html",
                        controller: "RegisterController",
                        controllerAs: "registerVm"
                    }
                }
            })
            .state('profile', {
                url: "/profile",
                data: {
                    pageTitle: "Shopifine | Profile"
                },
                views: {
                    'content@': {
                        templateUrl: "app/components/user/user-profile.html",
                        controller: "UserProfileController",
                        controllerAs: "profileVm"
                    }
                }
            })
            .state('shopping-cart', {
                url: "/cart",
                data: {
                    pageTitle: "Shopifine | Shopping Cart"
                },
                views: {
                    'content@': {
                        templateUrl: "app/components/shopping/shopping-cart.html",
                        controller: "ShoppingCartController",
                        controllerAs: "cartVm"
                    }
                }
            })
            .state('bill', {
                url: "/bill/:id",
                data: {
                    pageTitle: "Shopifine | Bill"
                },
                views: {
                    'content@': {
                        templateUrl: "app/components/bills/single-bill.html",
                        controller: "SingleBillController",
                        controllerAs: "billVm"
                    }
                }
            });

            $httpProvider.interceptors.push(['$localStorage', '_', function ($localStorage, _) {
                return {
                    // Set Header to Request if user is logged
                    'request': function (config) {
                        var user = $localStorage.user;

                        if (!_.isUndefined(user)) {
                            config.headers['Authorization'] = 'Bearer ' + user.token;
                        }

                        return config;
                    }
                };
            }]);
    });
