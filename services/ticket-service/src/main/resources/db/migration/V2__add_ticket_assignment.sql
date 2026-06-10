alter table tickets add column required_skill varchar(80) not null default 'GENERAL';
alter table tickets add column assignment_status varchar(20) not null default 'UNASSIGNED';
alter table tickets add column assigned_technician_id uuid;
alter table tickets add column assigned_technician_name varchar(120);
alter table tickets add column assigned_technician_email varchar(254);

alter table tickets alter column required_skill drop default;
alter table tickets alter column assignment_status drop default;

create index idx_tickets_assignment_status on tickets (assignment_status);
