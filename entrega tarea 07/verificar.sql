select version, description, success from flyway_schema_history order by installed_rank;
select 'alumnos'::regclass::oid;
select id, matricula, nombre from alumnos;
