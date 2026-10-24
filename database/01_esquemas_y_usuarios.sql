-- =====================================================================
-- ExpressNow · Esquemas y usuarios de base de datos
--
-- Patrón Database per Service: cada microservicio tiene su propio
-- esquema y un usuario que SOLO tiene permisos sobre ese esquema.
--
-- Uso:
--   * Local: docker compose lo ejecuta automáticamente al crear el contenedor.
--   * Amazon RDS: ejecutarlo una vez conectado con el usuario maestro
--     (admin), cambiando antes las contraseñas '..._local'.
--
-- Las TABLAS no se crean aquí: las genera Hibernate a partir de las
-- entidades JPA al iniciar cada microservicio (ddl-auto=update).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS usuarios_db       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS envios_db         CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS transportistas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS seguimiento_db    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'ms_usuarios'@'%'       IDENTIFIED BY 'ms_usuarios_local';
CREATE USER IF NOT EXISTS 'ms_envios'@'%'         IDENTIFIED BY 'ms_envios_local';
CREATE USER IF NOT EXISTS 'ms_transportistas'@'%' IDENTIFIED BY 'ms_transportistas_local';
CREATE USER IF NOT EXISTS 'ms_seguimiento'@'%'    IDENTIFIED BY 'ms_seguimiento_local';

GRANT ALL PRIVILEGES ON usuarios_db.*       TO 'ms_usuarios'@'%';
GRANT ALL PRIVILEGES ON envios_db.*         TO 'ms_envios'@'%';
GRANT ALL PRIVILEGES ON transportistas_db.* TO 'ms_transportistas'@'%';
GRANT ALL PRIVILEGES ON seguimiento_db.*    TO 'ms_seguimiento'@'%';

FLUSH PRIVILEGES;
