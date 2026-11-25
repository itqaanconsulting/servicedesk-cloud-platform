-- Idempotency key of the reservation that made a technician BUSY (the ticket id).
-- Unique, so a retried reservation can never reserve a second technician for the same ticket.
alter table technicians add column reservation_key varchar(100);
create unique index ux_technicians_reservation_key on technicians (reservation_key);
