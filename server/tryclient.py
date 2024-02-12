import socket, threading
from Crypto.Util.number import *
import base64
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives.asymmetric import rsa, dh
from cryptography.hazmat.primitives import serialization, hashes, padding
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.kdf.hkdf import HKDF
import time
import os
import random
from client_messages import client_messages


SERVER_HOST = '192.168.1.6'
SERVER_PORT = 3000

class Client:
	def __init__(self):
		self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.client_socket.connect((SERVER_HOST, SERVER_PORT))

	def generateKey(self):
		self.loadparam()
		self.private_key = self.parameters.generate_private_key()
		self.public_key = self.private_key.public_key()
		print('sudah selesai generate key')

	def deriveKey(self):
		self.shared_key = self.private_key.exchange(self.holder_public_key)
		print("ini sharkeynya lur", self.shared_key, base64.b64encode(self.shared_key));
		# self.derived_key = HKDF(
		# 	algorithm=hashes.SHA256(),
		# 	length=32,
		# 	salt=None,
		# 	info=b'handshake data',
		# ).derive(self.shared_key)
		self.derived_key = self.shared_key[0:16]
		print("ini derivkeynya lur", self.derived_key, base64.b64encode(self.derived_key));

	def sendparam(self):
		self.serialized_parameters = self.parameters.parameter_bytes(serialization.Encoding.PEM, serialization.ParameterFormat.PKCS3)
		self.send(b"PARM||" + self.serialized_parameters.hex().encode())
		responseData = self.receive()
		if b"PARM_ACC" not in responseData:
			self.client_socket.close()
			exit()

	def loadparam(self):
		print('hereee')
		responseData = bytes.fromhex(self.client_socket.recv(1024).strip().decode())
		print(responseData)
		if b"PUB@@" in responseData[:5]:
			response = base64.b64decode(responseData[5:])
			self.holder_public_key = serialization.load_der_public_key(response, backend=default_backend())
			print(self.holder_public_key)
			
			self.parameters = self.holder_public_key.parameters()
			print('acumalaka', self.parameters)
		else:
			self.client_socket.close()
			exit()

	def keyExchange(self):
		self.sendpubkey()
		self.deriveKey()

	def sendpubkey(self):
		self.serialized_public_key = self.public_key.public_bytes(serialization.Encoding.DER, serialization.PublicFormat.SubjectPublicKeyInfo)
		self.send((b"PUB" + b"@@" + base64.b64encode(self.serialized_public_key)).hex().encode())
		print((b"PUB" + b"@@" + base64.b64encode(self.serialized_public_key)).hex())
		print('sudah terkirim key nya')

	def initialization(self):
		self.generateKey()
		self.keyExchange()

	def main(self):
		self.initialization()		
		# exit()
		while True:
			self.recvmessage()
			self.sendmessage()

		self.client_socket.close()
		exit()

	def encrypt(self, message):
		iv = os.urandom(16)
		cipher = Cipher(algorithms.AES(self.derived_key), modes.CBC(iv), backend=default_backend())
		encryptor = cipher.encryptor()

		padder = padding.PKCS7(algorithms.AES.block_size).padder()
		padded_message = padder.update(message.encode()) + padder.finalize()

		ciphertext = iv + encryptor.update(padded_message) + encryptor.finalize()
		return ciphertext.hex().encode()

	def decrypt(self, message):
		message = bytes.fromhex(message)
		iv = message[:16]
		message = message[16:]
		cipher = Cipher(algorithms.AES(self.derived_key), modes.CBC(iv), backend=default_backend())
		decryptor = cipher.decryptor()
		plaintext = decryptor.update(message) + decryptor.finalize()

		unpadder = padding.PKCS7(algorithms.AES.block_size).unpadder()

		unpadded_message = unpadder.update(plaintext) + unpadder.finalize()

		return unpadded_message

	def send(self, message):
		time.sleep(1)
		self.client_socket.sendall(message)

	def sendmessage(self):
		message = random.choice(client_messages)
		print("Kasinik = ", message)
		data = self.encrypt(message)
		self.send(data)

	def receive(self):
		response = self.client_socket.recv(4096).strip().decode()
		if not response:
			self.client_socket.close()
			exit()
		return response

	def recvmessage(self):
		try:
			response = self.receive()
			data = self.decrypt(response).decode()
			print("Kisanak = ", data)
		except Exception as e:
			print("error recv", e)
			self.client_socket.close()
			exit()



if __name__ == '__main__':
	client = Client()
	client.main()
