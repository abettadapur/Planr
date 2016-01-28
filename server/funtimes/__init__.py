import flask
from flask import Flask, render_template
from flask_restful import Api
from flask.ext.sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data/temp.db'

db = SQLAlchemy(app)

import funtimes.routes