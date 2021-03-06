"""empty message

Revision ID: f07d4f5de5a1
Revises: 1bebb9c46001
Create Date: 2016-03-06 22:19:19.529922

"""

# revision identifiers, used by Alembic.
revision = 'f07d4f5de5a1'
down_revision = '1bebb9c46001'

from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import mysql

def upgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('itinerary', 'city')
    ### end Alembic commands ###


def downgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.add_column('itinerary', sa.Column('city', mysql.VARCHAR(length=200), nullable=False))
    ### end Alembic commands ###
