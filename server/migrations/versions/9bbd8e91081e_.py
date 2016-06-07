"""empty message

Revision ID: 9bbd8e91081e
Revises: cc89380ee408
Create Date: 2016-03-02 15:50:15.280897

"""

# revision identifiers, used by Alembic.
revision = '9bbd8e91081e'
down_revision = 'cc89380ee408'

from alembic import op
import sqlalchemy as sa


def upgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.create_table('rating',
    sa.Column('id', sa.String(length=300), nullable=False),
    sa.Column('title', sa.String(length=200), nullable=False),
    sa.Column('rating', sa.Integer(), nullable=False),
    sa.Column('content', sa.String(length=1000), nullable=False),
    sa.Column('user_id', sa.Integer(), nullable=True),
    sa.Column('itinerary_id', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['itinerary_id'], ['itinerary.id'], name=op.f('fk_rating_itinerary_id_itinerary')),
    sa.ForeignKeyConstraint(['user_id'], ['user.id'], name=op.f('fk_rating_user_id_user')),
    sa.PrimaryKeyConstraint('id', name=op.f('pk_rating'))
    )
    ### end Alembic commands ###


def downgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('rating')
    ### end Alembic commands ###