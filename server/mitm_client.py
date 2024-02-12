import socket
import threading
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives import serialization, hashes, padding
from cryptography.hazmat.backends import default_backend


import base64

SERVER_HOST = '192.168.1.7'
SERVER_PORT = 3001
DEFAULT_KEY = b'\x00' * 16

def encrypt(message):
	cipher = Cipher(algorithms.AES(DEFAULT_KEY), modes.ECB(), backend=default_backend())
	encryptor = cipher.encryptor()

	padder = padding.PKCS7(algorithms.AES.block_size).padder()
	padded_message = padder.update(message.encode()) + padder.finalize()

	ciphertext = encryptor.update(padded_message) + encryptor.finalize()
	return base64.b64encode(ciphertext)

def decrypt(message):
	message = base64.b64decode(message)
	cipher = Cipher(algorithms.AES(DEFAULT_KEY), modes.ECB(), backend=default_backend())
	decryptor = cipher.decryptor()
	plaintext = decryptor.update(message) + decryptor.finalize()

	unpadder = padding.PKCS7(algorithms.AES.block_size).unpadder()

	unpadded_message = unpadder.update(plaintext) + unpadder.finalize()

	return unpadded_message


def receive_messages(client_socket):
    try:
        while True:
            response = client_socket.recv(1024)
            if not response:
                break
            decrypted = decrypt(response)
            print("Received:", decrypted.decode())
    except Exception as e:
        print("An error occurred while receiving messages.", e)
    finally:
        client_socket.close()

client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client.connect((SERVER_HOST, SERVER_PORT))

receive_thread = threading.Thread(target=receive_messages, args=(client,))
receive_thread.start()

try:
    while True:
        message = input("Enter a message: ")
        encrypted = encrypt(message)
        client.send(encrypted)
except KeyboardInterrupt:
    print("Client terminated by user.")
finally:
    client.close()
