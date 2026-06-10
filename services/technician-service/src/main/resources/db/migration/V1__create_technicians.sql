create table technicians (
    id uuid primary key,
    name varchar(120) not null,
    email varchar(254) not null unique,
    availability varchar(20) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table technician_skills (
    technician_id uuid not null references technicians (id) on delete cascade,
    skill varchar(80) not null,
    primary key (technician_id, skill)
);

create index idx_technicians_availability on technicians (availability);
create index idx_technician_skills_skill on technician_skills (skill);
