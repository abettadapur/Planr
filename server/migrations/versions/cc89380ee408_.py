"""empty message

Revision ID: cc89380ee408
Revises: f9f293d4688a
Create Date: 2016-03-02 15:37:51.959589

"""

# revision identifiers, used by Alembic.
revision = 'cc89380ee408'
down_revision = 'f9f293d4688a'

from alembic import op
import sqlalchemy as sa


def upgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.add_column('yelp_item', sa.Column('review_count', sa.Integer(), nullable=True))
    ### end Alembic commands ###


def downgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('yelp_item', 'review_count')
    ### end Alembic commands ###
