const myUrl = "http://localhost:8080/item/get-items";
const myUrl2 = "list/update-order-status";
const chai = require("chai");
const chaiHttp = require("chai-http");
const server = require("../app");
const should = chai.should();
const expect = chai.expect();
const request = require("request");
chai.use(chaiHttp);

describe('returns items', function() {
    it('returns items', function(done) {

    it('post a list', function(done) {
        var obj = {};
        obj._id = "driverJohn@gmail.com";
        obj.status = "Completed";
    
        chai.request(server).post(myUrl2).send(obj).end((request, response) => {
            console.log(JSON.stringify(obj) + '\n');
            console.log(JSON.stringify(response) + '\n');
            done();
          });
    });

        request.get({ url: myUrl },
        function(error, response, body) {
                console.log( body + '\n');
            done();
        });
    });

});