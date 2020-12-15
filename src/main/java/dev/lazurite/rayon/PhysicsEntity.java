    public void updatePositionAndAngles(Vector3f position, float yaw, float pitch) {
        this.updatePositionAndAngles(position.x, position.y, position.z, yaw, pitch);
        physics.setPosition(position);
        setYaw(yaw);
    }

    protected void setPlayerID(int playerID) {
        Entity entity = getEntityWorld().getEntityById(playerID);
        if (entity instanceof PlayerEntity) {
            setCustomName(new LiteralText(((PlayerEntity) entity).getGameProfile().getName()));
            setCustomNameVisible(true);
        }
    }
