
CREATE KEYSPACE IF NOT EXISTS ks_iot WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
CREATE TABLE ks_iot.t_message (c_device text , c_date TIMESTAMP, c_value bigint ,PRIMARY KEY (c_device,c_date,c_value));
CREATE TABLE ks_iot.t_aggregation (c_device text , c_date TIMESTAMP, c_min bigint, c_max bigint, c_avg bigint, c_med bigint ,PRIMARY KEY (c_device,c_date));
