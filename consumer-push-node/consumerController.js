const request = require('request');

function consumerController(consumerId, brokerConfirmMessageEndpoint, confirmMessages) {
    function healthcheck(req, res) {
        res.status(200);
        res.send();
    };

    function pushConsumerMessage(req, res) {
        const messageId = req.body.messageId;
        const body = req.body.body;

        console.log(`Message received: ${messageId} - ${body}`);

        if (confirmMessages) {
            confirmMessage = {
                messageId: messageId,
                consumerId: consumerId
            }

            const options = {
                  uri: brokerConfirmMessageEndpoint,
                  method: 'POST',
                  json: confirmMessage
            };

            request(options, (err, res, body) => {
                if (!err && res.statusCode === 200) {
                    console.log('Message confirmed...');
                }
            });
        }

        res.status(200);
        res.send();
    };

    return {
        healthcheck,
        pushConsumerMessage
    }
}

module.exports = consumerController;