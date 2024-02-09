delete from trade_deals;
delete from cars;
delete from car_owners;
delete from models;
insert into models (model_name, model_year, company, engine_capacity, engine_power) values 
('model1', 2020, 'company1',1300, 84),
('model1', 2021, 'company1',1300, 90),
('model2', 2020, 'company2',2000, 110),
('model3', 2021, 'company3',2000, 110),
('model4', 2023, 'company4',2500, 160);
insert into car_owners (id, name,  email, birth_date) values 
(123, 'name1',  'name1@gmail.com', '2000-10-10'), 
(124, 'name2',  'name2@gmail.com', '1990-12-20'), 
(125, 'name3',  'name3@gmail.com', '1975-12-10');
insert into cars (car_number, color, kilometers, car_state, model_name, model_year, owner_id) values 
('111-11-111', 'red', 1000, 'GOOD', 'model1', 2020, 123),
('222-11-111', 'silver', 10000, 'OLD', 'model1', 2020, 124),
('333-11-111', 'white', 0, 'NEW', 'model4', 2023, 125);

insert into trade_deals ( date, car_number, owner_id) values 
( '2023-03-10', '111-11-111', 123),
( '2023-03-24', '222-11-111', 124),
( '2023-04-01', '333-11-111', 125);