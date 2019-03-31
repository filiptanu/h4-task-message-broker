const express = require('express');
const os = require('os');
const request = require('request');
const bodyParser = require('body-parser');

const app = express();

const port = process.env.PORT || 8081;
const consumerId = process.env.CONSUMER_ID;
const brokerSubscribeEndpoint = process.env.BROKER_SUBSCRIBE_ENDPOINT || 'http://localhost:8080/subscribe';
const brokerConfirmMessageEndpoint = process.env.BROKER_CONFIRM_MESSAGE_ENDPOINT || 'http://localhost:8080/confirmMessage';
const confirmMessages = process.env.CONFIRM_MESSAGES || true;

if (!brokerSubscribeEndpoint) {
    console.log('CONSUMER_ID environment variable must be provided... Exiting...');
    process.exit(-1);
}

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

const consumerRouter = require('./consumerRouter')(consumerId, brokerConfirmMessageEndpoint, confirmMessages);
app.use('/', consumerRouter);

app.server = app.listen(port, () => {
    console.log(`Running on port ${port}...`);

    subscribeAtBroker();
});

function subscribeAtBroker() {
    console.log('Subscribing at broker...');

    const hostName = os.hostname();

    const healthcheckEndpoint = `http://${hostName}:${port}/healthcheck`;
    const pushEndpoint = `http://${hostName}:${port}/pushConsumerMessage`;

    const subscribeConsumerMessage = {
        consumerId: consumerId,
        healthcheckEndpoint: healthcheckEndpoint,
        pushEndpoint: pushEndpoint
    }

    const options = {
          uri: brokerSubscribeEndpoint,
          method: 'POST',
          json: subscribeConsumerMessage
    };

    request(options, (err, res, body) => {
        if (!err && res.statusCode === 200) {
            console.log('Successfully registered at broker...')
        } else {
            console.log('There was a problem with registering at the broker... Shutting down the consumer...');
            process.exit(-1);
        }
    });
}