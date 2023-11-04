l = dlmread('temperature.dat', ',');
l = l';

l = l(1:end-1);
A = zeros(length(l), 2);

A(:,1) = 1;
A(:,2) = 0 : 1 : length(l)-1;

x_hat = inv(A'*A) * A' * l

time_vec = A(:,2);
x = time_vec;
k = x_hat(2)
d = x_hat(1)
y = k*x + d;

figure;
plot([l, y], 'LineWidth', 2); hold on;
%plot(y);