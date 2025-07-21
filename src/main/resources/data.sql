-- Insert default corporate if not exists
INSERT INTO corporates (name, active, type, hospital_code, bill_parser_identifier, teritary_hospital, communication_in_string)
SELECT 'ASTER PRIME HOSPITAL, HYDERABAD, ANDHRA PRADESH', true, 'HOSPITAL', 'HOS-3206', 1, false, '{"claim": {"sms": null, "email": []}, "query": {"sms": null, "email": []}}'
WHERE NOT EXISTS (SELECT 1 FROM corporates WHERE hospital_code = 'HOS-3206'); 