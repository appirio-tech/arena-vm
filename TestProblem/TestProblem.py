class Base:
	def sum(self, a, b):
		return a + b

class TestProblem(Base):
	def sum(self, a, b):
		try:
			with open('/etc/passwd', 'r') as f:
				print('Open /etc/passwd is allowed')
				close(f)
		except Exception as e:
			print('Open /etc/passwd is dis-allowed: ', e)
        # super() is new in Python3
		return super().sum(a, b)