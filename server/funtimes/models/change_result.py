class ChangeResult(object):
    def __init__(self):
        self.errors = []
        self.changes = []

    def success(self):
        return len(self.errors) == 0

    def add_child_result(self, child_result):
        for error in child_result.errors:
            self.errors.append(error)
