const express = require('express');
const app = express();
const pg = require("pg");

app.use(express.urlencoded({ extended: true }));
app.use(express.json());

var client;
let legacy_app_url = "http://xxxx/telecom/apis/changeplan";

async function db_connect() {
  return new Promise(function (resolve, reject) {
    let vcapLocal = getVCAPLocal();
    let caCert = new Buffer(vcapLocal.connection.postgres.certificate.certificate_base64, 'base64');
    let connectionString = vcapLocal.connection.postgres.composed[0];
    console.log("DB Connection String: " + connectionString);

    const parse = require('pg-connection-string').parse;
    let config = parse(connectionString);

    config.ssl = {
      ca: caCert
    }

    client = new pg.Client(config); client.connect(function (err) {
      if (err) {
        console.log(err);
        reject(error);
        process.exit(1);
      } else {
        console.log("CONNECTION ESTABLISHED");
        resolve("CONNECTION ESTABLISHED");
      }
    });
  });
}

current_plan_query = "SELECT a.name, a.description, a.data_limit, a.cost  from public.plan as a, public.customer_plan as b, public.customer as c WHERE a.id = b.plan_id AND b.customer_id = c.id AND c.mobile = $1";
latest_bill_query = "SELECT b.amount, max(b.bill_date), b.usage from public.customer as a, public.billing as b where a.mobile = $1 and a.id = b.customer_id group by b.amount, b.usage";
usage_query = "SELECT b.amount, b.bill_date, b.usage from public.customer as a, public.billing as b where a.mobile = $1 and a.id = b.customer_id ORDER BY b.bill_date DESC FETCH FIRST 3 ROWS ONLY";
name_query = "SELECT salutation, first_name, last_name FROM public.customer WHERE mobile=$1";
recommendation_query = "SELECT name, description, data_limit, cost FROM plan WHERE data_limit >= (SELECT AVG(usage) from billing, customer where billing.customer_id = customer.id AND customer.mobile = $1) ORDER by data_limit ASC FETCH FIRST 1 ROWS ONLY";
average_usage_query = "SELECT AVG(usage) from billing, customer where billing.customer_id = customer.id AND customer.mobile = $1";

let currency_notation = "$";
let data_size_notation = "GB";

async function queryDB(query, mobile_no) {
  if( !client ){
    await db_connect();
  }
    console.log("Mobile no: " + mobile_no);
  console.log("DB query: " + query);
  return new Promise(function (resolve, reject) {
    client.query(
      query, [mobile_no],
      function (error, result) {
        if (error) {
          console.log("Error querying database: " + JSON.stringify(error));
          reject(error);
        } else {
          console.log("DB Query result: " + JSON.stringify(result.rows));
          resolve(result);
        }
      }
    );
  });
}


async function main(data) {
  console.log("");
  console.log("REQUEST TYPE: " + data.request_type);
  if (data.request_type === 'CURRENT_PLAN') {
    try {
      var result = await queryDB(current_plan_query, data.mobile_no);
      if (isDataFound(result)) {
        var rStr = "Your current plan details are:<br>Plan: " + result.rows[0].name;
        rStr = rStr + "<br>Description: " + result.rows[0].description;
        rStr = rStr + "<br>Data Limit: " + result.rows[0].data_limit + " " + data_size_notation;
        rStr = rStr + "<br>Cost: " + currency_notation + result.rows[0].cost;
        console.log("Response data: " + rStr);
        return { "response": rStr };
      } else {
        console.log("No data found");
        return { "response": "No data found" };
      }
    } catch (err) {
      console.log("Error - " + JSON.stringify(err));
      return { "response": "Error querying database", "Error description": JSON.stringify(err) };
    }
  }
  else if (data.request_type === 'LATEST_BILL') {
    try {
      var result = await queryDB(latest_bill_query, data.mobile_no);
      if (isDataFound(result)) {
        console.log("result = " + JSON.stringify(result.rows[0]));
        var response = {
          "Amount": currency_notation + result.rows[0].amount,
          "Month": getMontYear(result.rows[0].max),
          "Usage": result.rows[0].usage + " " + data_size_notation
        };
        // Your latest bill details are as follows:<br>Bill Month: $webhook_result_1.Month<br>Usage: $webhook_result_1.Usage<br>Amount: $webhook_result_1.Amount
        var rStr = "Your latest bill details are as follows:<br>Bill Month: " + getMontYear(result.rows[0].max);
        rStr = rStr + "<br>Usage: " + result.rows[0].usage + " " + data_size_notation;
        rStr = rStr + "<br>Amount: " + currency_notation + result.rows[0].amount;
        console.log("Response data: " + rStr);

        return { "response": rStr };
      } else {
        console.log("No data found");
        return { "response": "No data found" };
      }
    } catch (err) {
      console.log("Error - " + JSON.stringify(err));
      return { "response": "Error querying database", "Error description": JSON.stringify(err) };
    }
  }
  else if (data.request_type === 'USAGE_DATA') {
    try {
      var result = await queryDB(usage_query, data.mobile_no);
      if (isDataFound(result)) {
        var i;
        var rStr = "";
        for (i = result.rows.length - 1; i >= 0; i--) {
          rStr = rStr + "Month: " + getMontYear(result.rows[i].bill_date) + ", ";
          rStr = rStr + "Amount: " + currency_notation + result.rows[i].amount + ", ";
          rStr = rStr + "Usage: " + result.rows[i].usage + " " + data_size_notation + "<br>";
        }
        rStr = "Your usage details of last 3 months are:<br> " + rStr;
        console.log("Response data: " + rStr);
        return { "response": rStr };
      } else {
        console.log("No data found");
        return { "response": "No data found" };
      }
    } catch (err) {
      console.log("Error - " + JSON.stringify(err));
      return { "response": "Error querying database", "Error description": JSON.stringify(err) };
    }
  }
  else if (data.request_type === 'NAME') {
    try {
      var result = await queryDB(name_query, data.mobile_no);
      if (isDataFound(result)) {
        salutation = result.rows[0].salutation.includes(".") ? result.rows[0].salutation : result.rows[0].salutation + ".";
        var rStr = "Welcome, ";
        rStr = rStr + salutation + " " + result.rows[0].first_name + " " + result.rows[0].last_name + ". ";
        rStr = rStr + "How can I help you today?"
        console.log("Response data: " + JSON.stringify(response));

        return { "response": rStr, "isMobileNumValid": 1 };
      } else {
        console.log("Mobile number entered is incorrect");
        var rStr = "Mobile number entered is incorrect."
        return { "response": "Mobile number entered is incorrect", "isMobileNumValid": 0 };
      }
    } catch (err) {
      console.log("Error - " + JSON.stringify(err));
      return { "Error summary": "Error querying database", "Error description": JSON.stringify(err) };
    }
  }
  else if (data.request_type === 'RECOMMENDATION') {
    var avg;
    var current_limit;
    try {
      var average_usage_result = await queryDB(average_usage_query, data.mobile_no);
      var current_limit_result = await queryDB(current_plan_query, data.mobile_no);
      if (isDataFound(average_usage_result) && isDataFound(current_limit_result)) {
        var avg = average_usage_result.rows[0].avg;
        var current_limit = current_limit_result.rows[0].data_limit;
        var plan_name = current_limit_result.rows[0].name;
        if (avg <= current_limit) {
          console.log("Recommendation: Current plan is best for your usage. PLAN = " + plan_name);
          return { "response": "Current plan," + plan_name + ", is best for your usage", "code": 0 };
        } else {
          var recommendation_query_result = await queryDB(recommendation_query, data.mobile_no);
          if (isDataFound(recommendation_query_result)) {
            var rStr = "Here is a recommendation for you, based on previous 3 months usage data: <br>";
            rStr = rStr + "Plan Name: " + recommendation_query_result.rows[0].name + "<br>";
            rStr = rStr + "Description: " + recommendation_query_result.rows[0].description + "<br>";
            rStr = rStr + "Data Limit: " + recommendation_query_result.rows[0].data_limit + " " + data_size_notation + "<br>";
            rStr = rStr + "Cost:" + currency_notation + recommendation_query_result.rows[0].cost + "<br>";
            var response = {
              "response": rStr
            };
            console.log("Recommendation response data: " + JSON.stringify(response));
            return { "response": rStr, "code": 1, "plan": recommendation_query_result.rows[0].name };
          } else {
            console.log("No data found for recommendation query");
            return { "response": "No data found for recommendation query" };
          }
        }
      } else {
        console.log("No data found for average_usage_query or current_plan_query");
        return { "response": "No data found for average_usage_query or current_plan_query" };
      }
    } catch (err) {
      console.log("Error - " + JSON.stringify(err));
      return { "response": "Error querying database", "Error description": JSON.stringify(err) };
    }
  }
  else if (data.request_type === 'UPDATE_PLAN') {
    console.log("In update plan");
    const response = await updatePlan(data);
    return response;
  }
  else {
    console.log("request_type not matching");
    return { "response": "request_type not matching" }
  }
}

async function updatePlan(data) {
  return new Promise(function (resolve, reject) {
    var request = require('request');
    var options = {
      'method': 'POST',
      'url': legacy_app_url,
      'headers': {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      form: {
        'mobile': data.mobile_no,
        'plan': data.plan
      }
    };
    request(options, function (error, response) {
      if (error) {
        console.log("Error occurred while updating plan details");
        reject({ "response": "Error occurred while updating plan details" });
      }
      console.log("Update plan request response: " + response.body);
      resolve({ "response": "Plan update request submitted successfully." });
    });
  });
}


function isDataFound(result) {
  if (result && result.rows && result.rows.length > 0) {
    return true;
  } else {
    return false;
  }
}


module.exports = app;

function getVCAPLocal() {
  let vcapLocal = {
    "connection": {
      "cli": {
        "arguments": [
          [
            "host=xxxxxx port=xxxxx dbname=ibmclouddb user=xxxx sslmode=verify-full"
          ]
        ],
        "bin": "psql",
        "certificate": {
          "certificate_base64": "xxxxxxxxxxxxxxxxxxxxxxx",
          "name": "xxxx"
        },
        "composed": [
          "PGPASSWORD=xxxx psql 'host=xxxxx port=xxxx dbname=ibmclouddb user=xxxx sslmode=verify-full'"
        ],
        "environment": {
          "PGPASSWORD": "xxxx",
          "PGSSLROOTCERT": "xxxx"
        },
        "type": "cli"
      },
      "postgres": {
        "authentication": {
          "method": "direct",
          "password": "xxxx",
          "username": "xxxx"
        },
        "certificate": {
          "certificate_base64": "xxxxxxxxxxx",
          "name": "xxxxx"
        },
        "composed": [
          "postgres://xxxxx:xxxxx@xxxx:xxxxx/demodb?sslmode=verify-full"
        ],
        "database": "ibmclouddb",
        "hosts": [
          {
            "hostname": "xxxx",
            "port": xxxx
          }
        ],
        "path": "/ibmclouddb",
        "query_options": {
          "sslmode": "verify-full"
        },
        "scheme": "postgres",
        "type": "uri"
      }
    },
    "instance_administration_api": {
      "deployment_id": "xxxx:a/xxx::",
      "instance_id": "xxxx",
      "root": "https://xxxx"
    }
  }
  return vcapLocal;
}

function getMontYear(cdate) {
  d = new Date(cdate);
  month = cdate.toLocaleString('default', { month: 'long' });
  year = cdate.getFullYear();
  return month + " " + year;
}