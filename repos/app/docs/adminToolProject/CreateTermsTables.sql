-- This script creates tables to maintain the terms for each round and to
-- save the agreements of users registered to contest round with round terms.
-- round_terms table contains content of terms agreement for each round,
-- round_terms_acceptance contains d
--

create table round_terms (
    round_id DECIMAL(10,0),
    terms_content TEXT
);

create table round_terms_acceptance (
    round_id DECIMAL(10,0),
    user_id  DECIMAL(10,0),
    timestamp DATETIME YEAR TO FRACTION
);

