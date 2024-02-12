import socket
import threading
import os
import signal
import sys

# Target host and port
TARGET_HOST = 'localhost'
TARGET_PORT = 3001

# Proxy server port
PROXY_PORT = 3000

class Attacker:
	def handle_client(self):
		try:
			while True:
				client_data = self.client_socket.recv(4096)
				if not client_data:
					print('disconnected')
					exit()
				print('client:', client_data.decode())
				client_to_server = input("client (tamper): ").encode()
				if client_to_server == b"fw": #forward message
					client_to_server = client_data
				elif client_to_server == b"ex":
					self.attacker_socket.close()
					self.client_socket.close()
					self.server_socket.close()

					sys.exit(0)
				self.server_socket.sendall(client_to_server)
			self.attacker_socket.close()
		except Exception as e:
			print("something's wrong1", e)

	def handle_server(self):
		try:
			while True:
				server_data = self.server_socket.recv(4096)
				if not server_data:
					print('disconnected')
					exit()
				print('server:', server_data.decode())
				server_to_client = input("server (tamper): ").encode()
				if server_to_client == b"fw": #forward message
					server_to_client = server_data
				elif server_to_client == b"ex":
					self.attacker_socket.close()
					self.client_socket.close()
					self.server_socket.close()
					sys.exit(0)
				self.client_socket.sendall(server_to_client)
			self.attacker_socket.close()
		except Exception as e:
			print("something's wrong2", e)
	

	def handle_incoming(self):
		server_thread = threading.Thread(target=self.handle_server)
		server_thread.start()

		client_thread = threading.Thread(target=self.handle_client)
		client_thread.start()

	def main(self):
		try:
			self.attacker_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.attacker_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
			self.attacker_socket.bind(('0.0.0.0', PROXY_PORT))
			self.attacker_socket.listen(1)
		
			self.client_socket, client_addr = self.attacker_socket.accept()
			print('accepted connection from', client_addr)
		
			self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.server_socket.connect((TARGET_HOST, TARGET_PORT))
			def signal_handler(signal, frame):
				sys.exit(0)
			signal.signal(signal.SIGINT, signal_handler)
			
			self.handle_incoming()
		except Exception as e:
			print("something's wrong", e)


if __name__ == '__main__':
	attacker = Attacker()
	attacker.main()
