"""First commit

Revision ID: 9cee9d84f065
Revises: 
Create Date: 2022-02-07 20:50:09.974090

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '9cee9d84f065'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('user_data',
    sa.Column('uid', sa.String(), nullable=False),
    sa.Column('user_completed_onboarding', sa.Boolean(), nullable=True),
    sa.Column('name', sa.String(), nullable=True),
    sa.Column('age', sa.Integer(), nullable=True),
    sa.PrimaryKeyConstraint('uid')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('user_data')
    # ### end Alembic commands ###