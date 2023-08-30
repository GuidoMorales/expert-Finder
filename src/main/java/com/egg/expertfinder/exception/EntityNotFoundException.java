package com.egg.expertfinder.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityType, Object entityId) {
        super("No se encontró la entidad de tipo " + entityType.getSimpleName() + " con ID " + entityId);
    }
}

