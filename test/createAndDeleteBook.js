const assert = require('assert');
const request = require('request');
const fs = require('fs');
const endpoints = require('../endpoints.json');

describe('Create, Delete', function() {
    this.timeout(60000);
    it('should create a new Book, & delete it', done => {
        const path = endpoints['listBooks']['GET'];
        const desiredPayload = require("./data/createBook.json");
        const options = {'url' : path, 'form': JSON.stringify(desiredPayload)};

        request.post(options, (err, res, body) => {
            if (err){
                throw new Error("Create call failed: " + err);
            }
            assert.equal(200, res.statusCode, "Create Status Code != 200 (" + res.statusCode + ")");
            const book = JSON.parse(res.body);
            // Now delete the book
            const deletePath = path + "/" + book.id;
            request.del(deletePath, function (err, res, body){
                if(err){
                    throw new Error("Delete call failed: " + err);
                }
                assert.equal(204, res.statusCode, "Delete Status Code != 200 (" + res.statusCode + ")");
                done();
            });
        });
    });
});