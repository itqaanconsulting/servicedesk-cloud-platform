create table tickets (
    id uuid primary key,
    title varchar(120) not null,
    description varchar(2000) not null,
    requester_email varchar(254) not null,
    priority varchar(20) not null,
    status varchar(20) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create index idx_tickets_status on tickets (status);
create index idx_tickets_priority on tickets (priority);
