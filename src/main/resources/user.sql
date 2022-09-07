-- adding roles
insert into roles (name) values ('ADMIN');
insert into roles (name) values ('USER');

-- adding admin user
insert into users (hash, reg_time, balance, name, password) values ('hash', '2022-05-17 16:22:05.847', 100000, 'Admin', '$2a$10$4s23sD6tp2TF.OgIM4DD7uk5OVkXiispVYixwCb.AbinLvztRygEy');
insert into user_contact (approved, code, code_time, code_trails, contact, type, user_id) values (1, 'code', '2022-05-17 16:22:06.165', 1, 'admin@admin.com', '1', 1);
insert into user2roles (user_id, role_id) values (1, 1);

-- adding test user
insert into users (hash, reg_time, balance, name, password) values ('hash', '2022-05-17 16:22:05.847', 100000, 'Dmitrii Petrov', '$2a$10$4s23sD6tp2TF.OgIM4DD7uk5OVkXiispVYixwCb.AbinLvztRygEy');
insert into user_contact (approved, code, code_time, code_trails, contact, type, user_id) values (1, 'code', '2022-05-17 16:22:06.165', 1, 'rifatg13@gmail.com', '1', 2);
insert into user_contact (approved, code, code_time, code_trails, contact, type, user_id) values (1, 'code', '2022-05-17 16:22:06.165', 1, '79297591719', '0', 2);
insert into user2roles (user_id, role_id) values (2, 2);