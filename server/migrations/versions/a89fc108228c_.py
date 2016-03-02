"""empty message

Revision ID: a89fc108228c
Revises: 8778dbfc1fcc
Create Date: 2016-02-19 20:54:07.954285

"""

# revision identifiers, used by Alembic.
revision = 'a89fc108228c'
down_revision = '8778dbfc1fcc'

from alembic import op
import sqlalchemy as sa


def upgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.create_table('yelp_item',
    sa.Column('id', sa.Integer(), autoincrement=False, nullable=False),
    sa.Column('name', sa.String(length=200), nullable=False),
    sa.Column('image_url', sa.String(length=300), nullable=True),
    sa.Column('url', sa.String(length=300), nullable=True),
    sa.Column('phone', sa.String(length=15), nullable=True),
    sa.Column('rating', sa.Integer(), nullable=True),
    sa.Column('yelp_location_id', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['yelp_location_id'], ['yelp_location.id'], name=op.f('fk_yelp_item_yelp_location_id_yelp_location')),
    sa.PrimaryKeyConstraint('id', name=op.f('pk_yelp_item'))
    )
    op.add_column('item', sa.Column('yelp_item_id', sa.Integer(), nullable=True))
    op.create_foreign_key(op.f('fk_item_yelp_item_id_yelp_item'), 'item', 'yelp_item', ['yelp_item_id'], ['id'])
    op.add_column('yelp_category', sa.Column('end_time', sa.Time(), nullable=True))
    op.add_column('yelp_category', sa.Column('start_time', sa.Time(), nullable=True))
    ### end Alembic commands ###


def downgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('yelp_category', 'start_time')
    op.drop_column('yelp_category', 'end_time')
    op.drop_constraint(op.f('fk_item_yelp_item_id_yelp_item'), 'item', type_='foreignkey')
    op.drop_column('item', 'yelp_item_id')
    op.drop_table('yelp_item')
    ### end Alembic commands ###