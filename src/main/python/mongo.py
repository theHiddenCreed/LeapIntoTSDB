
from pymongo import MongoClient

import os
from dotenv import load_dotenv

load_dotenv()

MONGO_CONNECTION_STRING = os.getenv('QUARKUS_MONGODB_CONNECTION_STRING')

class Database:
	'''https://www.mongodb.com/docs/manual/reference/operator/query/'''

	def __init__(self, name: str):
		self.uri = MONGO_CONNECTION_STRING
		self.name = name
		
		self.client = MongoClient(
			self.uri,
			connect = True,
			socketTimeoutMS = 30000
		)
		
		self.db = self.client[self.name]

	def close(self):
		self.client.close()

	def insert(self, collection: str, data: list):
		self.db[collection].insert_many(data, ordered = False)

	def read(self, collection: str, **args):
		return list(self.db[collection].find(args, {'_id': False}))

	def delete(self, collection: str, **args):
		self.db[collection].delete_many(args)

	def update(self, collection: str, condition: dict, new_doc: dict):
		self.db[collection].update_many(
			condition,
			{'$set': new_doc}
		)
