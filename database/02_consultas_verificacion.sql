-- =====================================================================
-- Consultas para mostrar en el video que los datos quedan persistidos.
-- Ejecutar en MySQL Workbench / DBeaver / cliente mysql conectado a RDS.
-- =====================================================================

-- ms-usuarios: usuarios y sus direcciones (OneToMany)
SELECT u.id, u.rut, u.nombre, u.email, u.tipo, d.calle, d.numero, d.comuna, d.principal
FROM usuarios_db.usuarios u
LEFT JOIN usuarios_db.direcciones d ON d.usuario_id = u.id;

-- La contraseña se guarda como hash BCrypt, nunca en texto plano
SELECT id, email, password_hash FROM usuarios_db.usuarios;

-- ms-transportistas: transportistas, vehículos y asignaciones
SELECT t.id, t.nombre, t.comuna_base, t.disponible, v.patente, v.tipo
FROM transportistas_db.transportistas t
LEFT JOIN transportistas_db.vehiculos v ON v.transportista_id = t.id;

SELECT * FROM transportistas_db.asignaciones;

-- ms-envios: envíos con sus paquetes e historial de estados
SELECT e.id, e.codigo_seguimiento, e.estado, e.transportista_id, e.costo, p.descripcion, p.peso_kg
FROM envios_db.envios e
JOIN envios_db.paquetes p ON p.envio_id = e.id;

SELECT envio_id, estado_anterior, estado_nuevo, comentario, fecha
FROM envios_db.historial_estados
ORDER BY envio_id, fecha, id;

-- ms-seguimiento: línea de tiempo del envío
SELECT s.codigo_seguimiento, s.estado_actual, e.estado, e.descripcion, e.ubicacion, e.fecha_evento
FROM seguimiento_db.seguimientos s
JOIN seguimiento_db.eventos_seguimiento e ON e.seguimiento_id = s.id
ORDER BY s.id, e.fecha_evento, e.id;
