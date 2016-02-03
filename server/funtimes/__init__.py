import flask
import os
from flask import Flask, render_template
from flask_restful import Api
from flask.ext.sqlalchemy import SQLAlchemy
from funtimes.etc import config

basedir = os.path.abspath(os.path.dirname(__file__))

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = config.SQLALCHEMY_DATABASE_URI
app.config['SQLALCHEMY_MIGRATE_REPO'] = config.SQLALCHEMY_MIGRATE_REPO

db = SQLAlchemy(app)
db.metadata.naming_convention = {
    'pk': 'pk_%(table_name)s',
    'fk': 'fk_%(table_name)s_%(column_0_name)s_%(referred_table_name)s',
    'uq': 'uq_%(table_name)s_%(column_0_name)s',
    'ix': 'ix_%(table_name)s_%(column_0_name)s',
    # 'ck': 'ck_%(table_name)s_%(constraint_name)s',
}

import funtimes.routes


@app.errorhandler(500)
def on_unhandled_exception():
    return '{"error":"An unknown server error occurred"}'
