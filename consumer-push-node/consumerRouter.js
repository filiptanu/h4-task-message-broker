const express = require('express');
const consumerController = require('./consumerController.js');

function routes(consumerId, brokerConfirmMessageEndpoint, confirmMessages) {
    const consumerRouter = express.Router();
    const controller = consumerController(consumerId, brokerConfirmMessageEndpoint, confirmMessages);

    consumerRouter
        .route('/healthcheck')
        .get(controller.healthcheck);
    consumerRouter
        .route('/pushConsumerMessage')
        .post(controller.pushConsumerMessage);

    return consumerRouter;
}

module.exports = routes;