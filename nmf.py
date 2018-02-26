import numpy as np


def grad_mf(data, factors, steps=5000, lr=0.0002, reg=0.02):
    """
    Implements non-negative matrix factorization with regularization.
    The loss function is minimized using stochastic gradient descent.
    """
    w = np.random.rand(data.shape[0], factors)
    h = np.random.rand(data.shape[1], factors).T
    for _ in range(steps):
        for i in range(len(data)):
            for j in range(len(data[i])):
                if data[i][j] > 0:
                    eij = data[i][j] - np.dot(w[i, :], h[:, j])
                    for k in range(factors):
                        uw = w[i][k] + lr * (2 * eij * h[k][j] - reg * w[i][k])
                        uh = h[k][j] + lr * (2 * eij * w[i][k] - reg * h[k][j])

                        w[i][k] = 0 if uw < 0 else uw
                        h[k][j] = 0 if uh < 0 else uh
    return w, h


def nmf(X, k, max_iter=5000):
    """
    Implements NMF using multiplicative update rule (Lee and Seung, 2001).
    """
    def difcost(a, b):
        dif = 0
        for i in range(a.shape[0]):
            for j in range(a.shape[1]):
                dif += pow(a[i, j] - b[i, j], 2)
        return dif

    ic = X.shape[0]
    fc = X.shape[1]

    w = np.matrix([[np.random.random() for _ in range(k)] for _ in range(ic)])
    h = np.matrix([[np.random.random() for _ in range(fc)] for _ in range(k)])

    for i in range(max_iter):
        wh = w * h
        cost = difcost(X, wh)
        print i, cost # DEBUG
        if cost == 0:
            break

        # Update feature matrix
        hn = (w.T * X)
        hd = (w.T * w * h)
        h = np.matrix(np.array(h) * np.array(hn) / np.array(hd))

        # Update weights matrix
        wn = (X * h.T)
        wd = (w * h * h.T)
        w = np.matrix(np.array(w) * np.array(wn) / np.array(wd))
    return w, h


def wnmf(data, k, max_iter=100, err_lim=1e-6, fit_err_lim=1e-6):
    """
    Implements a variant of multiplicative update rule that does not
    reconstruct zeros in the input matrix.
    """
    eps = 1e-5
    mask = np.sign(data)

    rows, columns = data.shape
    w = np.random.rand(rows, k)
    w = np.maximum(w, eps)

    h = np.random.rand(k, columns)
    h = np.maximum(h, eps)

    masked = mask * data
    est_prev = np.dot(w, h)
    for i in range(1, max_iter + 1):
        top = np.dot(masked, h.T)
        bottom = (np.dot((mask * np.dot(w, h)), h.T)) + eps
        w *= top / bottom
        w = np.maximum(w, eps)

        top = np.dot(w.T, masked)
        bottom = np.dot(w.T, mask * np.dot(w, h)) + eps
        h *= top / bottom
        h = np.maximum(h, eps)

        if i % 50 == 0 or i == max_iter:
            est = np.dot(w, h)
            err = mask * (est_prev - est)
            fit_residual = np.sqrt(np.sum(err ** 2))
            est_prev = est

            err = np.linalg.norm(mask * (data - est), ord='fro')
            if err < err_lim or fit_residual < fit_err_lim:
                break

    return w, h, est, err


if __name__ == '__main__':
    # zeros code missing entries in the matrix
    X = np.array([[2, 0, 1, 2, 0], [0, 2, 1, 0, 1], [1, 0, 0, 2, 2], [2, 1, 0, 0, 1]])
    k = 4

    # note how simple approach reconstructs zeros in X
    W, H = nmf(X, k, 100)
    print(W, H)
    print(np.round(np.dot(W, H)))
    print('----')

    # with WNMF, zeros are now treated as missing entries...
    w, h, res, err = wnmf(X, k, 100)
    print(np.round(np.dot(w, h), 2))
    print('----')

    # ... and gradient descend also does the job
    w, h = grad_mf(X, k, 1000)
    print(np.round(np.dot(w, h), 2))
