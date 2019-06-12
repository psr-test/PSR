var assert = require('assert');
var request = require('request');
var fs = require('fs');

describe('Create, Delete', function() {
    this.timeout(5000);
    it('should create a new Book, & deletes it', function(done) {
        // Build and log the path
        var path = "https://" + process.env.ENDPOINT + "/books/create";

        // Fetch the comparison payload
        require.extensions['.txt'] = function (module, filename) {
            module.exports = fs.readFileSync(filename, 'utf8');
        };
        var desiredPayload = require("./data/createBook.json");

        // Create the new todo
        var options = {'url' : path, 'form': JSON.stringify(desiredPayload)};
        request.post(options, function (err, res, body){
            if (err){
                throw new Error("Create call failed: " + err);
            }
            assert.equal(200, res.statusCode, "Create Status Code != 200 (" + res.statusCode + ")");
            var book = JSON.parse(res.body);
            // Now delete the book
            var deletePath = path + "/" + book.id;
            request.del(deletePath, function (err, res, body){
                if(err){
                    throw new Error("Delete call failed: " + err);
                }
                assert.equal(200, res.statusCode, "Delete Status Code != 200 (" + res.statusCode + ")");
                done();
            });
        });
    });
});