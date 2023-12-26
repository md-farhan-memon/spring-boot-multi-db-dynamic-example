## Seed Data
Run following commands on psql cli to generate dummy/sample/seed data for the project

````
CREATE DATABASE "multi-db-demo-1";
\c multi-db-demo-1;
CREATE TABLE sample (
	id uuid NOT NULL,
	name character varying DEFAULT 'multi-db-demo-1'::character varying NOT NULL
);
ALTER TABLE ONLY sample ADD CONSTRAINT sample_pkey PRIMARY KEY (id);
INSERT INTO sample (id, name) VALUES ((select gen_random_uuid ()), 'multi-db-demo-1');

CREATE DATABASE "multi-db-demo-2";
\c multi-db-demo-2;
CREATE TABLE sample (
	id uuid NOT NULL,
	name character varying DEFAULT 'multi-db-demo-2'::character varying NOT NULL
);
ALTER TABLE ONLY sample ADD CONSTRAINT sample_pkey PRIMARY KEY (id);
INSERT INTO sample (id, name) VALUES ((select gen_random_uuid ()), 'multi-db-demo-2');

CREATE DATABASE "multi-db-demo-3";
\c multi-db-demo-3;
CREATE TABLE sample (
	id uuid NOT NULL,
	name character varying DEFAULT 'multi-db-demo-3'::character varying NOT NULL
);
ALTER TABLE ONLY sample ADD CONSTRAINT sample_pkey PRIMARY KEY (id);
INSERT INTO sample (id, name) VALUES ((select gen_random_uuid ()), 'multi-db-demo-3');
````

## Multiple Database Configuration
`merchant_data_sources.json` inside resources acts as a list of multiple data sources to dynamically switch to during runtime.
Following are properties required

* `merchantId` - Unique String for the source which helps in identification and runtime switching context
* `dbUrl` - Database URL along with connection scheme and database name e.g. `jdbc:postgresql://localhost:5433/multi-db-demo-1`,
* `dbUsername` - Databse Username
* `dbPassword` - Database Password


## Demo
Once you start the application, you can hit the url by changing path variable and the response is received from the corresponding databse.

#### Request
````
curl localhost:8080/sample/merchant-id-2
````

#### Response
````
[{"id":"6244315b-be4d-400d-a950-fbe59d7bab38","name":"multi-db-demo-2"}]
````
