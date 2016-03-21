__author__ = 'Sarah'

with open("graph.txt") as f:
    content = f.readlines()
    for line in content:
        line_array = line.split('\t')
        node1 = line_array[0]
        node2 = line_array[1]
        weight = line_array[14]
        combined = node1 + "\t" + node2 + "\t" + weight
        with open("new-graph.txt", "a") as myfile:
                myfile.write(combined)