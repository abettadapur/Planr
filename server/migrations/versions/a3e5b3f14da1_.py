"""empty message

Revision ID: a3e5b3f14da1
Revises: a89fc108228c
Create Date: 2016-02-20 10:14:54.345291

"""

# revision identifiers, used by Alembic.
revision = 'a3e5b3f14da1'
down_revision = 'a89fc108228c'

from alembic import op
import sqlalchemy as sa
from sqlalchemy.sql import text


def upgrade():
    command = text("""
        UPDATE yelp_category
        SET start_time = "08:00:00", end_time="11:00:00"
        WHERE name = "breakfast"
    """)
    op.get_bind().execute(command)

    command = text("""
        UPDATE yelp_category
        SET start_time = "11:00:00", end_time="2:00:00"
        WHERE name = "lunch"
    """)
    op.get_bind().execute(command)

    command = text("""
        UPDATE yelp_category
        SET start_time = "2:00:00", end_time="6:00:00"
        WHERE name = "attraction"
    """)
    op.get_bind().execute(command)

    command = text("""
        UPDATE yelp_category
        SET start_time = "6:00:00", end_time="8:00:00"
        WHERE name = "dinner"
    """)
    op.get_bind().execute(command)

    command = text("""
        UPDATE yelp_category
        SET start_time = "8:00:00", end_time="12:00:00"
        WHERE name = "nightlife"
    """)
    op.get_bind().execute(command)


def downgrade():
    pass
