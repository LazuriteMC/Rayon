public final class PhysicsWorld extends DiscreteDynamicsWorld {
    public void track(@NotNull Entity entity) {
        PhysicsEntityComponent component = PhysicsEntityComponent.get(entity);

        if (entities.contains(entity) || component == null) {
            throw new DynamicBodyException("Entity is not registered.");
        } else {
            entities.add(entity);
        }
    }

    /**
     * Get a list of rigid bodies. Includes blocks.
     * @return a list of rigid bodies
     */
    public List<RigidBody> getRigidBodies() {
        List<RigidBody> bodies = Lists.newArrayList();

        /* Add all blocks. */
        bodies.addAll(this.blockHelper.getRigidBodies());

        /* Add all entities. */
        entities.forEach(entity -> {
//            DynamicBodyComposition physics = ((DynamicBody) entity).getDynamicBody();
//
//            if (physics != null) {
//                bodies.add(physics.getRigidBody());
//            }
        });

        return bodies;
    }
}
